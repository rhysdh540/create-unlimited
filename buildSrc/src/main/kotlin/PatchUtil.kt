import io.github.prcraftmc.classdiff.ClassDiffer
import io.github.prcraftmc.classdiff.format.DiffWriter
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode

object PatchUtil {
	fun shrinkPatchForDirty(cleanBytes: ByteArray, dirtyBytes: ByteArray): ByteArray {
		val dirty = readClass(dirtyBytes)

		dirty.methods.forEach { method ->
			method.parameters?.removeIf { it.name == null }
		}

		return ClassWriter(ClassReader(cleanBytes), 0).also {
			dirty.accept(it)
		}.toByteArray()
	}

	fun generatePatchBytes(clean: ByteArray, dirty: ByteArray): ByteArray {
		val cleanClass = readClass(clean)
		val dirtyClass = readClass(dirty)
		return DiffWriter().also {
			ClassDiffer.diff(cleanClass, dirtyClass, it)
		}.toByteArray()
	}
}

fun readClass(bytes: ByteArray): ClassNode {
	val classNode = ClassNode()
	ClassReader(bytes).accept(classNode, 0)
	return classNode
}