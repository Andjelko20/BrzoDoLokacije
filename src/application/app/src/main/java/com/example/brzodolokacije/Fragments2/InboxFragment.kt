package com.example.brzodolokacije.Fragments2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Adapters.HomePostAdapter
import com.example.brzodolokacije.Adapters.InboxAdapter
import com.example.brzodolokacije.Adapters.MessageAdapter
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.ModelsDto.InboxDto
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
 * Use the [InboxFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InboxFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var chatList : MutableList<InboxDto>

    private var myMessageAdapter : RecyclerView.Adapter<InboxAdapter.MainViewHolder>? = null
    private var mylayoutManager : RecyclerView.LayoutManager? = null
    private lateinit var inboxRecyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_inbox, container, false)
        val list : MutableList<InboxDto> = mutableListOf()
        chatList = list
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val exitInbox= view.findViewById<Button>(R.id.exitInbox)
        exitInbox.setOnClickListener{
            requireActivity().finish()
        }

        val retrofit = Client(requireActivity()).buildService(Api::class.java)
        retrofit.getInbox().enqueue(object: retrofit2.Callback<DefaultResponse>
        {
            override fun onResponse(
                call: Call<DefaultResponse>,
                response: Response<DefaultResponse>
            ) {
                if(response.body()?.error.toString() == "false")
                {
                    val res = response.body()?.message.toString()
                    //Log.d("inboxresponse", res.toString())
                    val typeToken = object : TypeToken<MutableList<InboxDto>>() {}.type
                    val inbox = Gson().fromJson<MutableList<InboxDto>>(res, typeToken)
                    chatList = inbox
                    //Log.d("inbox",inbox.toString())
                    val rvInbox= view.findViewById<RecyclerView>(R.id.rvInbocChats)
                    rvInbox.layoutManager = LinearLayoutManager(context)
                    rvInbox.adapter = context?.let { InboxAdapter(chatList, it,requireActivity()) }
                }
                else
                {
                    val rvInbox= view.findViewById<RecyclerView>(R.id.rvInbocChats)
                    rvInbox.layoutManager = LinearLayoutManager(context)
                    rvInbox.setHasFixedSize(true)
                    rvInbox.adapter = context?.let { InboxAdapter(chatList, it,requireActivity()) }
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Log.d("inboxError","")
            }

        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InboxFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InboxFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}