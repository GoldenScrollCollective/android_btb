package org.lemonadestand.btb.features.common.models.body

data class RecordRequestBody (
var  blessing_complete : String ,
var  blessing_sent : String ,
var  blessing_value : String ,
var  blessing_notes : String ,
var  blessing_by_user_id : String ,
var  repeating : String ="0" ,
var  frequency : String ="once" ,
var  start : String  ,
var  end : String  ,
var  parent : String =""  ,
var  resource : String   ,
)

