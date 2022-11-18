package com.example.brzodolokacije.Posts

object PrivremeniKomentar {
    fun getComments() :MutableList<Comment>
    {
        var list = mutableListOf<Comment>()

        list.add(Comment("1","owner1","","comment1",""))
        list.add(Comment("2","owner2","","comment2",""))
        list.add(Comment("3","owner3","","comment3",""))
        list.add(Comment("4","owner3","","comment4",""))
        list.add(Comment("5","owner2","","comment5",""))
        list.add(Comment("6","owner1","","comment6",""))
        list.add(Comment("7","owner4","","comment7",""))
        list.add(Comment("8","owner5","","comment8",""))
        list.add(Comment("9","owner6","","comment9",""))
        list.add(Comment("10","owner51","","comment10",""))

        return list

    }
}