package org.lemonadestand.btb.features.post.models

data class CommentModelDate (
    var commentList  :ArrayList<PostModel> = ArrayList(),
    var date : String ? = null
)