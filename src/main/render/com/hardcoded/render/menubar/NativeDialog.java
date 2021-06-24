package com.hardcoded.render.menubar;

import java.nio.ByteBuffer;

import org.lwjgl.system.*;

/**
 * Cannot select folders.
 * 
 * @deprecated Only works for windows x64.
 * @author HardCoded
 */
public class NativeDialog {
	private static final SharedLibrary COMDLG32 = Library.loadNative(NativeDialog.class, "com.hardcoded.render", "comdlg32");
	private static final long GetOpenFileNameW_ptr;
	private static final long GetSaveFileNameW_ptr;
	private static final long CommDlgExtendedError_ptr;
	
	static {
		// BOOL GetOpenFileNameW( LPOPENFILENAMEW unnamedParam1 );
		GetOpenFileNameW_ptr = APIUtil.apiGetFunctionAddress(COMDLG32, "GetOpenFileNameW");
		
		// BOOL GetSaveFileNameW( LPOPENFILENAMEW unnamedParam1 );
		GetSaveFileNameW_ptr = APIUtil.apiGetFunctionAddress(COMDLG32, "GetSaveFileNameW");
		
		// DWORD CommDlgExtendedError();
		CommDlgExtendedError_ptr = APIUtil.apiGetFunctionAddress(COMDLG32, "CommDlgExtendedError");
	}
	
	public static final int
		OFN_READONLY				= 0x00000001,
		OFN_OVERWRITEPROMPT			= 0x00000002,
		OFN_HIDEREADONLY			= 0x00000004,
		OFN_NOCHANGEDIR				= 0x00000008,
		OFN_SHOWHELP				= 0x00000010,
		OFN_ENABLEHOOK				= 0x00000020,
		OFN_ENABLETEMPLATE			= 0x00000040,
		OFN_ENABLETEMPLATEHANDLE	= 0x00000080,
		OFN_NOVALIDATE				= 0x00000100,
		OFN_ALLOWMULTISELECT		= 0x00000200,
		OFN_EXTENSIONDIFFERENT		= 0x00000400,
		OFN_PATHMUSTEXIST			= 0x00000800,
		OFN_FILEMUSTEXIST			= 0x00001000,
		OFN_CREATEPROMPT			= 0x00002000,
		OFN_SHAREAWARE				= 0x00004000,
		OFN_NOREADONLYRETURN		= 0x00008000,
		OFN_NOTESTFILECREATE		= 0x00010000,
		OFN_NONETWORKBUTTON			= 0x00020000,
		OFN_NOLONGNAMES				= 0x00040000,
		OFN_EXPLORER				= 0x00080000,
		OFN_NODEREFERENCELINKS		= 0x00100000,
		OFN_LONGNAMES				= 0x00200000,
		OFN_ENABLEINCLUDENOTIFY		= 0x00400000,
		OFN_ENABLESIZING			= 0x00800000,
		OFN_DONTADDTORECENT			= 0x02000000,
		OFN_FORCESHOWHIDDEN			= 0x10000000;
	
	public static final int
		CDERR_STRUCTSIZE			= 0x0001,
		CDERR_INITIALIZATION		= 0x0002,
		CDERR_NOTEMPLATE			= 0x0003
	;
	
	// Windows 10 x64
	public static final int MAX_PATH = 260;
	public static final int NULL = 0;
	
	public static final int CommDlgExtendedError() {
		return JNI.callI(CommDlgExtendedError_ptr);
	}
	
	public static final boolean GetOpenFileNameW(OPENFILENAMEW ofn) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			ByteBuffer struct = stack.malloc(152);
			struct.put(0, new byte[152]);
			long struct_address = MemoryUtil.memAddress(struct);
			ofn.put(struct);
			return JNI.callPI(struct_address, GetOpenFileNameW_ptr) != 0;
		}
	}
	
	public static final int GetSaveFileNameW(OPENFILENAMEW ofn) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			ByteBuffer struct = stack.malloc(152);
			struct.put(0, new byte[152]);
			long struct_address = MemoryUtil.memAddress(struct);
			ofn.put(struct);
			return JNI.callPI(struct_address, GetSaveFileNameW_ptr);
		}
	}
	
	public static class OPENFILENAMEW {
		final int lStructSize = 152;	//DWORD         lStructSize;
		long hwndOwner;					//HWND          hwndOwner;
		long hInstance;					//HINSTANCE     hInstance;
		long lpstrFilter;				//LPCWSTR       lpstrFilter;
		long lpstrCustomFilter;			//LPWSTR        lpstrCustomFilter;
		int nMaxCustFilter;				//DWORD         nMaxCustFilter;
		int nFilterIndex;				//DWORD         nFilterIndex;
		long lpstrFile;					//LPWSTR        lpstrFile;
		int nMaxFile;					//DWORD         nMaxFile;
		long lpstrFileTitle;			//LPWSTR        lpstrFileTitle;
		int nMaxFileTitle;				//DWORD         nMaxFileTitle;
		long lpstrInitialDir;			//LPCWSTR       lpstrInitialDir;
		long lpstrTitle;				//LPCWSTR       lpstrTitle;
		int Flags;						//DWORD         Flags;
		short nFileOffset;				//WORD          nFileOffset;
		short nFileExtension;			//WORD          nFileExtension;
		long lpstrDefExt;				//LPCWSTR       lpstrDefExt;
		long lCustData;					//LPARAM        lCustData;
		long lpfnHook;					//LPOFNHOOKPROC lpfnHook;
		long lpTemplateName;			//LPCWSTR       lpTemplateName;
		long pvReserved;				//void          *pvReserved;
		int dwReserved;					//DWORD         dwReserved;
		int FlagsEx;					//DWORD         FlagsEx;
		
		private void put(ByteBuffer buffer) {
			buffer.mark();
			buffer
				.putLong(lStructSize) // DWORD
				.putLong(hwndOwner)
				.putLong(hInstance)
				.putLong(lpstrFilter)
				.putLong(lpstrCustomFilter)
				.putInt(nMaxCustFilter)
				.putInt(nFilterIndex)
				.putLong(lpstrFile)
				.putLong(nMaxFile) // DWORD
				.putLong(lpstrFileTitle)
				.putLong(nMaxFileTitle) // DWORD
				.putLong(lpstrInitialDir)
				.putLong(lpstrTitle)
				.putInt(Flags)
				.putShort(nFileOffset)
				.putShort(nFileExtension)
				.putLong(lpstrDefExt)
				.putLong(lCustData)
				.putLong(lpfnHook)
				.putLong(lpTemplateName)
				.putLong(pvReserved)
				.putInt(dwReserved)
				.putInt(FlagsEx);
			
			buffer.reset();
		}
	}
}
