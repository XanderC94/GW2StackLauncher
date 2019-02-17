package model.ontologies.gw2

//import com.google.gson.*
//import java.lang.reflect.Type

data class LocalAddOnsWrapper(val addOns: List<LocalAddOn>) {

    companion object : () -> LocalAddOnsWrapper {

        override fun invoke(): LocalAddOnsWrapper {
            return LocalAddOnsWrapper(listOf())
        }
    }
}