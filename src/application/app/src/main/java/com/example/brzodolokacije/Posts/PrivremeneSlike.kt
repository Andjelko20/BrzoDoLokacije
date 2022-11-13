package com.example.brzodolokacije.Posts

object PrivremeneSlike {
    private var photos= mutableListOf<Photo>()

    public fun getPhotos() : List<Photo>
    {
        photos.add(Photo("1","https://a.travel-assets.com/findyours-php/viewfinder/images/res70/84000/84621-Paris.jpg","owner1", "11/06/2022","Paris, France","City of love and light",123,13))
        photos.add(Photo("2","https://www.travelmagazine.rs/wp-content/uploads/2021/08/Berlin-kapija.jpg","owner2", "13/12/2021","Berlin, Germany","Opis2",13,3))
        photos.add(Photo("3","https://www.collegiate-ac.pt/propeller/uploads/sites/4/2020/11/ruas-mais-bonitas-de-lisboa-1-1450x967.jpg","owner1", "11/11/2022","Lisbon, Portugal","Neki drugi opis ne znam",250,17))
        photos.add(Photo("4","https://www.telegraph.co.uk/content/dam/Travel/Destinations/Europe/Russia/Moscow/moscow-night-guide-lead.jpg","owner3","23/04/2022","Moscow, Russia","Neki drugi opis ne znam",10,1))
        photos.add(Photo("5","https://assets.vogue.com/photos/58e2b6c4889049239005e60b/master/w_2560%2Cc_limit/00-holding-belgrade-serbia-travel-guide.jpg","owner5", "07/02/2022","Belgrade, Serbia","Neki drugi opis ne znam",90,23))
        photos.add(Photo("6","http://london.com/wp-content/uploads/2019/03/london_001.jpg","owner8", "18/05/2021","London, Great Britain","Neki drugi opis ne znam",53,4))
        photos.add(Photo("7","https://www.gannett-cdn.com/-mm-/6cb0566daad6b8973e2c456e7a61df50f54939b5/c=0-117-1408-1994/local/-/media/2017/01/04/USATODAY/USATODAY/636191149244091355-GettyImages-537365893.jpg","owner4", "21/06/2022","Tower of Pisa, Pisa, Italy","Neki drugi opis ne znam",85,8))
        photos.add(Photo("8","https://www.thomascook.com/.imaging/mte/thomascook-theme/og-image/dam/uk/holidays/city-breaks/paris-dekstop.jpg/jcr:content/paris-dekstop.jpg","owner6", "11/06/2022","Paris, France","Neki drugi opis ne znam",77,7))
        return photos
    }
}