package org.lemonadestand.btb.features.common.models.body


data class AddCommentBody (
    var uniq_id  :String,
    var resource  :String,
    var html  :String,
    var created  :String,
    var parent_id  :String,
    var modified  :String,
    var by_user_id  :String,
    var type  :String = "reply",
    var user  : ShareStoryUser,

    )
