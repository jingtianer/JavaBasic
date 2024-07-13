package kt_test.a

//import androidx.annotation.RestrictTo

open class KtClass {
    companion object {
        @JvmStatic
        val instance : Any
//            @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
            get() {
                return KtClass()
            }
    }
}