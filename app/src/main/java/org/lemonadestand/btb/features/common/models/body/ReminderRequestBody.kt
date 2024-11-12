package org.lemonadestand.btb.features.common.models.body

import java.util.Date

data class ReminderRequestBody (
    var start : Date,
    var end : Date,
    var title : String,
    var reminder : Date,
    var frequency : String,
    var repeating : String,
    var notify : ArrayList<String>,
    var blessing_complete : Any ? = null,
    var blessing_sent : String ? = null,
    var blessing_value : String ? = null,
    var blessing_notes : String ? = null,
    var blessing_by_user_id : String ? = null,
    var parent : String ? = null,
    var resource : String,
    var description : String
)
