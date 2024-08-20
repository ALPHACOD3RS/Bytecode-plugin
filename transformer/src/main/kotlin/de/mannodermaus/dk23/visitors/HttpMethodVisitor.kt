//package de.mannodermaus.dk23.visitors
//
//import org.objectweb.asm.MethodVisitor
//import org.objectweb.asm.Opcodes
//
///**
// * A visitor implementation that replaces parts of a class' `toString()` method
// * and removes all of the sensitive @Redacted information.
// */
//internal class HttpMethodVisitor(
//    api: Int,
//    next: MethodVisitor,
//    private val redactedParameterIndices: List<Int>,
//) : MethodVisitor(api, next) {
//
//    // The toString() method of a data class repeatedly loads each field
//    // of the primary constructor with a sequence of the following instructions:
//    //   1. ALOAD (local variable declaration)
//    //   2. GETFIELD (assign field to local variable)
//    //   3. INVOKEVIRTUAL (append to StringBuilder)
//    // Count how many times this happens within the method, and
//    // replace this bytecode for each of the "redacted parameter indices"
//    // with a hardcoded redacted value ("*******").
//    private var active = false
//    private var counter = -1
//
//    override fun visitVarInsn(opcode: Int, `var`: Int) {
//        // Always increment the counter to keep track of which parameter is being handled
//        counter++
//
//        // Enable a one-time replacement below
//        // whenever an ALOAD instruction is found.
//        // Skip that instruction in this case, removing it from the bytecode
//        if (opcode == Opcodes.ALOAD && counter in redactedParameterIndices) {
//            active = true
//        } else {
//            super.visitVarInsn(opcode, `var`)
//        }
//    }
//
//    override fun visitFieldInsn(
//        opcode: Int,
//        owner: String?,
//        name: String?,
//        descriptor: String?
//    ) {
//        // For GETFIELD instructions of parameters annotated with @Redacted,
//        // replace the bytecode with a constant value for the 'redacted value' instead
//        if (opcode == Opcodes.GETFIELD &&  owner == "okhttp3/Request\$Builder" && name == "url") {
////            visitLdcInsn("Authorization")
////            visitLdcInsn("Bearer siu")
//        } else {
//            super.visitFieldInsn(opcode, owner, name, descriptor)
//        }
//    }
//
//    override fun visitMethodInsn(
//        opcode: Int,
//        owner: String?,
//        name: String?,
//        descriptor: String?,
//        isInterface: Boolean
//    ) {
//        // When this field is replaced with a redacted value, always use the 'String' variant of StringBuilder.append().
//        // The original bytecode may use a different variant of this API (e.g. for integers)
//        if (opcode == Opcodes.INVOKEVIRTUAL && active && counter in redactedParameterIndices) {
//            active = false
//            super.visitMethodInsn(
//                opcode,
//                owner,
//                name,
//                "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
//                isInterface
//            )
//        } else {
//            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
//        }
//    }
//}

package de.mannodermaus.dk23.visitors

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * A visitor implementation that intercepts API calls made using HttpURLConnection
 * and adds an Authorization header to the request.
 */
internal class ApiServiceMethodVisitor(
    api: Int,
    next: MethodVisitor
) : MethodVisitor(api, next) {

    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        // Intercept calls to setRequestProperty on HttpURLConnection
        if (opcode == Opcodes.INVOKEVIRTUAL
            && owner == "java/net/HttpURLConnection"
            && name == "setRequestProperty"
            && descriptor == "(Ljava/lang/String;Ljava/lang/String;)V") {

            // Add the Authorization header
            super.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/net/HttpURLConnection",
                "setRequestProperty",
                "(Ljava/lang/String;Ljava/lang/String;)V",
                false
            )

            visitLdcInsn("Authorization")
            visitLdcInsn("siu")
        }

        // Continue with the original instruction
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
    }
}
