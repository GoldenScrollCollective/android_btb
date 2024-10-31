package org.lemonadestand.btb.features.post.models

data class CommentModelDate (
    var commentList  :ArrayList<Post> = ArrayList(),
    var date : String ? = null
)