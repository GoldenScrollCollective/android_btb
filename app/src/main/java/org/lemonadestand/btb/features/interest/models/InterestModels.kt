package org.lemonadestand.btb.features.interest.models

data class InterestResponseModel (
    val status: Boolean,
    val message: String,
    val data: List<InterestModel>? = emptyList()
)

data class InterestModel (
    val id: Long,
    val field_id: Long,
    val position: Long,
    val size: Long,
    var value: String ? = null,
    val parent_id: Any? = null,
    val label: String,
    val placeholder: String,
    val type: String,
    var options: List<Option>,
    val attributes: Any? = null,
    val required: Long,
    val company: String,
    val contact: String,
    val user: String,
    val event: String
)

data class Option (
    val label: String,
    val value: String,
    var isCheck : Boolean = false
)
