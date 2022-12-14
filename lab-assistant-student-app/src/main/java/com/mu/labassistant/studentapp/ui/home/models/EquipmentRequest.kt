package com.mu.labassistant.studentapp.ui.home.models

data class EquipmentRequest (

    val cost:Double?=null,

    val days: Int?=null,

    val equipment:Equipment?=null,

    val equipmentAdmin:String?=null,
    @field:JvmField
val isApproved:Boolean?=null,
    @field:JvmField
val isPaid:Boolean?=null,

val requestendday:Long?=null,

val requeststartday:Long?=null,

val userId:String?=null,
    var paymentcode:String?=null,
        )