package org.lemonadestand.btb.features.common.models.body


data class ShareStoryUser(
    var id  :String,
    var name  :String,
    var picture  :String ? = null ,
)

data class ShareStoryBody (
    var uniq_id  :String,
    var resource  :String,
    var html  :String,
    var media: String? = null,
    var created  :String,
    var parent_id  :String,
    var modified  :String,
    var by_user_id  :String,
    var user  : ShareStoryUser,
    var visibility: String ? = null,
    var anonymous: String = "0"
)
