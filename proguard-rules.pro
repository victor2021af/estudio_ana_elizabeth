# Modelos lidos/escritos pelo Gson precisam manter os nomes dos campos
-keep class br.com.estudioanaelizabeth.app.data.model.** { *; }

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod, RuntimeVisibleAnnotations, AnnotationDefault
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
