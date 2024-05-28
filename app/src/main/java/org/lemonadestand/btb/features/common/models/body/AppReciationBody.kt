package org.lemonadestand.btb.features.common.models.body


data class AppReciationBody (
    var uniq_id  :String,
    var resource  :String,
    var html  :String,
    var title  :String,
    var created  :String,
    var parent_id  :String,
    var by_user_id  :String,
    var modified  :String,
    var type  :String = "comment",
    var visibility:String,
    var user  : ShareStoryUser,
    var meta : AppReciationMeta,

    var users: ArrayList<String>
)


data class AppReciationMeta (
    var bonus : String,
    var debit : String,
)




