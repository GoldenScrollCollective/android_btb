package org.lemonadestand.btb.features.post.models

import java.util.Date

data class PostModelDate (
    var postList: ArrayList<Post> = ArrayList(),
    var date : Date? = null
)