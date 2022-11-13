package com.example.brzodolokacije.Posts

object PrivremeneSlikeZaFeed {
    private var posts= mutableListOf<String>()

    public fun getPhotos() : MutableList<String>
    {
        posts.add("https://a.travel-assets.com/findyours-php/viewfinder/images/res70/84000/84621-Paris.jpg")
        posts.add("https://www.travelmagazine.rs/wp-content/uploads/2021/08/Berlin-kapija.jpg")
        posts.add("https://www.collegiate-ac.pt/propeller/uploads/sites/4/2020/11/ruas-mais-bonitas-de-lisboa-1-1450x967.jpg")
        posts.add("https://www.telegraph.co.uk/content/dam/Travel/Destinations/Europe/Russia/Moscow/moscow-night-guide-lead.jpg")
        posts.add("https://assets.vogue.com/photos/58e2b6c4889049239005e60b/master/w_2560%2Cc_limit/00-holding-belgrade-serbia-travel-guide.jpg")
        posts.add("http://london.com/wp-content/uploads/2019/03/london_001.jpg")
        posts.add("https://www.gannett-cdn.com/-mm-/6cb0566daad6b8973e2c456e7a61df50f54939b5/c=0-117-1408-1994/local/-/media/2017/01/04/USATODAY/USATODAY/636191149244091355-GettyImages-537365893.jpg")
        posts.add("https://www.thomascook.com/.imaging/mte/thomascook-theme/og-image/dam/uk/holidays/city-breaks/paris-dekstop.jpg/jcr:content/paris-dekstop.jpg")
        return posts
    }
}