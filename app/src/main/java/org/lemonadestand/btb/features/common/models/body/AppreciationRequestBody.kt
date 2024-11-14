package org.lemonadestand.btb.features.common.models.body


data class AppreciationRequestBody (
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
    var meta : AppreciationMeta,
    var media: String? = null,
    var users: ArrayList<String>
)


data class AppreciationMeta (
    var bonus : String,
    var debit : String,
)




