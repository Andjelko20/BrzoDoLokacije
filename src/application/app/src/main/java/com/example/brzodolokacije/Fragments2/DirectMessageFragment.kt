package com.example.brzodolokacije.Fragments2

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.API.Api
import kotlinx.android.synthetic.main.fragment_direct_message.*
import com.example.brzodolokacije.Adapters.MessageAdapter
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Managers.InboxChatCommunicator
import com.example.brzodolokacije.Managers.SessionManager
import com.example.brzodolokacije.Managers.SignalRListener
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.ModelsDto.MessageDto
import com.example.brzodolokacije.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DirectMessageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DirectMessageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var user: String? = null
    private lateinit var isDirect : String

    private lateinit var signalRListener : SignalRListener
    private lateinit var messageList : MutableList<MessageDto>

    private lateinit var messageRecyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_direct_message, container, false)
        val chatingWithUser = view.findViewById<TextView>(R.id.chatingWithUser)
        user = arguments?.getString("username").toString()
        isDirect = arguments?.getString("directMessage").toString()
        chatingWithUser.text = user

        val list : MutableList<MessageDto> = mutableListOf()
        messageList=list

        signalRListener = SignalRListener.getInstance()
        messageRecyclerView = view.findViewById(R.id.rvMessages)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val exitDirectMessage = view.findViewById<Button>(R.id.exitDirectMessage)
        exitDirectMessage.setOnClickListener{
            if(isDirect=="direct message") requireActivity().finish()
            else
            {
                val communicator = activity as InboxChatCommunicator
                communicator.backToInbox()
            }
        }

        signalRListener.setDirectMessage(true)
        signalRListener.setRecycleView(rvMessages)
        signalRListener.setContext(context)
        signalRListener.setActivity(requireActivity())

        val retrofit = Client(requireActivity()).buildService(Api::class.java)
        retrofit.getMessages(user.toString()).enqueue(object: retrofit2.Callback<DefaultResponse> {
            override fun onResponse(
                call: Call<DefaultResponse>,
                response: Response<DefaultResponse>
            ) {
                if(response.body()?.error.toString() == "false")
                {
                    val res = response.body()?.message.toString()
                    val typeToken = object : TypeToken<MutableList<MessageDto>>() {}.type
                    val messages = Gson().fromJson<MutableList<MessageDto>>(res, typeToken)
                    messageList = messages
                    signalRListener.setList(messageList)
                }
                else
                {
                    Toast.makeText(requireActivity(), "An error occurred", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(requireActivity(), "An error occurred", Toast.LENGTH_SHORT).show()
            }
        })
        val sessionManager= this.context?.let { SessionManager(it) }
        val sendMessageBtn = view.findViewById<ImageView>(R.id.sendMessageBtn)
        val sendMessageText = view.findViewById<EditText>(R.id.sendMessageText)
        sendMessageBtn.setOnClickListener{
            if(sendMessageText.text.toString().trim() != "")
            {
                val message = sendMessageText.text.toString().trim()
                sendMessageText.text.clear()
                val sender = sessionManager?.fetchUsername()
                val receiver = user
                signalRListener.sendMessage(sender,receiver,message)
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DirectMessageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DirectMessageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}