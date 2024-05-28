package org.lemonadestand.btb.features.post.models

import org.lemonadestand.btb.features.post.models.PostModel

data class PostModelDate (
    var postList  :ArrayList<PostModel> = ArrayList(),
    var date : String ? = null
)