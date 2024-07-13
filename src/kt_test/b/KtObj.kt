package kt_test.b

open class KtClass{
    companion object {
        public val instance : KtClass
            get() {
                return KtClass()
            }
    }
}

object KtObj : KtClass() {
    fun default() : KtClass {
        return this
    }
}

fun test(kt:KtClass){
    println(kt)
}

fun main(args: Array<String>) {
    test(KtObj.default())
}