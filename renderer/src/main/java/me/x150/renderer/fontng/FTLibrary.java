package me.x150.renderer.fontng;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.freetype.FreeType;

import static org.lwjgl.util.freetype.FreeType.FT_Err_Ok;
import static org.lwjgl.util.freetype.FreeType.FT_Error_String;

/**
 * A holder of a FT_Library. Used to interact with freetype.
 */
public class FTLibrary extends RefWatcher {
	private static void handleFtErr(int e) {
		if (e != FT_Err_Ok) throw new IllegalStateException("FT error: " + FT_Error_String(e));
	}

	private final long library;

	/**
	 * Creates and allocates a new freetype library.
	 */
	public FTLibrary() {
		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			PointerBuffer ptrBuffer = memoryStack.mallocPointer(1);  // LibraryStruct**
			handleFtErr(FreeType.FT_Init_FreeType(ptrBuffer));
			library = ptrBuffer.get(0); // LibraryStruct*
		}
	}

	/**
	 * Gets the handle to the freetype library. Should be read only.
	 *
	 * @return Handle to freetype
	 */
	public long get() {
		checkClosed();
		return library;
	}

	@Override
	protected void implClose() {
		FreeType.FT_Done_FreeType(library);
	}
}
