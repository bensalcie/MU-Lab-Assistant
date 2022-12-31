package app.mu.mulabassistant.ui.home.models
data class Equipment (val  id:String?=null, val name:String?=null,val description:String?=null,
                      val cost:Double?=null,
                      @field:JvmField
                      val isbooked:Boolean?=null,
                      val imageone:String?=null,
                      val imagetwo:String?=null,
                      val imagethree:String?=null,val date:Long?=null,
                      val labcategory:String?=null,
                      val avaibalilitydate:Long?=null

        )


