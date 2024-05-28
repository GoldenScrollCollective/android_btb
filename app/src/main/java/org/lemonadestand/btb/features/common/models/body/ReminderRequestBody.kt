package org.lemonadestand.btb.features.common.models.body

data class ReminderRequestBody (
    var start : String,
    var end : String,
    var title : String,
    var reminder : String,
    var frequency : String,
    var repeating : String,
    var notify : ArrayList<String>,
    var blessing_complete : Any ? = null,
    var blessing_sent : String ? = null,
    var blessing_value : String ? = null,
    var blessing_notes : String ? = null,
    var blessing_by_user_id : String ? = null,
    var parent : String ? = null,
    var resource : String ,
    var description : String
)
