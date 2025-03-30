import com.palantir.javapoet.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.nio.file.Path
import javax.lang.model.element.Modifier
import kotlin.io.path.inputStream

private val matrixStackEntry = ClassName.get("net.minecraft.client.util.math", "MatrixStack", "Entry")
private val vertexConsumer = ClassName.get("net.minecraft.client.render", "VertexConsumer")

enum class VertexType(val count: Int, val type: TypeName, val consName: String, val hasTransform: Boolean, vararg val labels: String) {
    position(3, TypeName.FLOAT, "vertex", true, "x", "y", "z"),
    color(4, TypeName.FLOAT, "color", false, "r", "g", "b", "a"),
    normal(3, TypeName.FLOAT, "normal", true, "nx", "ny", "nz")
}

abstract class GeneratePrimitiveEmitterTask : DefaultTask() {
    @Serializable
    data class EmitterSpecification(val elements: List<VertexType>, val nFaces: Int,
                                    val nVerticesInFace: Int, val indices: List<Int>, val nInput: Int)
    @get:InputFile
    abstract val specificationFile: Property<String>

    @get:Input
    abstract val packageName: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @OptIn(ExperimentalSerializationApi::class)
    @TaskAction
    fun run() {
        val the = Path.of(specificationFile.get()).inputStream().use { Json.decodeFromStream<Map<String, EmitterSpecification>>(it) }
//        val mts = MethodSpec.methodBuilder("doShit")
//            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//            .returns(TypeName.VOID)
//        for (i in 0 until 10) {
//            mts
//                .addParameter(TypeName.FLOAT, "x$i")
//                .addParameter(TypeName.FLOAT, "y$i")
//                .addParameter(TypeName.FLOAT, "z$i")
//                .addParameter(TypeName.FLOAT, "r$i")
//                .addParameter(TypeName.FLOAT, "g$i")
//                .addParameter(TypeName.FLOAT, "b$i")
//                .addParameter(TypeName.FLOAT, "a$i")
//                .addParameter(TypeName.FLOAT, "nx$i")
//                .addParameter(TypeName.FLOAT, "ny$i")
//                .addParameter(TypeName.FLOAT, "nz$i")
//
//        }
//        val method = mts.build()
        val ts = TypeSpec.classBuilder("Emitter")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        the.entries.forEach {
            val emitMethod = buildEmitMethod(it.key, it.value)
            ts.addMethod(emitMethod)
        }
        val file = JavaFile.builder(packageName.get(), ts.build())
            .build()
        file.writeTo(outputDir.get().asFile)
    }

    private fun buildEmitMethod(name: String, spec: EmitterSpecification): MethodSpec {
        val builder = MethodSpec.methodBuilder("_emit_${name}__${spec.nInput}x${spec.elements.joinToString("_") { it.name }}")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(matrixStackEntry, "transform", Modifier.FINAL)
            .addParameter(vertexConsumer, "consumer", Modifier.FINAL)
        require(spec.nFaces * spec.nVerticesInFace == spec.indices.size) {"count of indices doesnt match nFaces * nVerticesInFace"}
        for(i in 0 until spec.nInput) {
            for (it in spec.elements) {
                for(j in 0 until it.count) {
                    val argName = "v${i}_${it.name}_${it.labels[j]}"
                    builder.addParameter(it.type, argName)
                }
            }
        }

        for (indices in spec.indices.windowed(spec.nVerticesInFace, spec.nVerticesInFace, false)) {
            val the = CodeBlock.builder()
            the.add("{\n").indent()
            the.add("// Vertices: \$L\n", indices.joinToString("-"))
            for (i in indices) {
                the.add("consumer")
                for (it in spec.elements) {
                    the.add(".\$L(", it.consName)
                    if (it.hasTransform) the.add("transform, ")
                    the.add((0 until it.count).joinToString(", ") { j -> "v${i}_${it.name}_${it.labels[j]}" })
                    the.add(")")
                }
                the.add(";\n")
            }
            the.endControlFlow()
            builder.addCode(the.build())
        }

        return builder.build()
    }
}