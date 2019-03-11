package sunhaixing.demo;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.User32Util;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
 

class COPYDATASTRUCT {
    COPYDATASTRUCT(Pointer pointer) {
    	dwData = pointer.getLong(0);
    	cbData = pointer.getLong(8);
    	Pointer pstr = pointer.getPointer(16);
    	strData = pstr.getString(0, "GBK");    	
	}
    
    public String getData() {
    	return strData;
    }
    
	long dwData;
	long cbData;
	String strData;
}

class CWindow {
    public CWindow() {
    }

    class WindowProc implements Callback {
        public WinDef.LRESULT callback(WinDef.HWND hwnd, WinDef.UINT uMsg, 
        		WinDef.WPARAM wParam, WinDef.LPARAM lParam) {
        	
            switch (uMsg.intValue()) {
            	case WinUser.WM_COPYDATA:            
            		COPYDATASTRUCT p = new COPYDATASTRUCT(
            				new Pointer(lParam.longValue()));
            		
            		System.out.println(p.getData());
            		
            		break;
                case WinUser.WM_CLOSE:
                    User32.INSTANCE.PostQuitMessage(0);
                    break;
            }
            return User32.INSTANCE.DefWindowProc(hwnd, uMsg.intValue(), wParam, lParam);
        }
    }

    public boolean Run() {
        String wndClassName = "JNADemo";
        String wndTitle = "JNADemo";

        WinDef.HINSTANCE hInstance = null;
        WinUser.WNDCLASSEX wndClassEx = new WinUser.WNDCLASSEX();
        wndClassEx.lpfnWndProc = new WindowProc();
        wndClassEx.cbClsExtra = 0;
        wndClassEx.cbWndExtra = 0;
        wndClassEx.style = 1|2;
        wndClassEx.lpszClassName = wndClassName;
        wndClassEx.hInstance = hInstance;
        wndClassEx.hCursor = null;
        wndClassEx.hIcon = null;
        wndClassEx.hbrBackground = null;
        wndClassEx.lpszMenuName = null;


        if(User32.INSTANCE.RegisterClassEx(wndClassEx).intValue()==0) {
            System.out.println("register window failed!");
            System.out.println(""+ Kernel32.INSTANCE.GetLastError() + "\n"+Kernel32Util.getLastErrorMessage());
            return false;
        }

        WinDef.HWND hWnd = User32Util.createWindowEx(0, wndClassName, wndTitle, WinUser.WS_OVERLAPPEDWINDOW,
                100, 100, 500, 400, null, null, hInstance, null);
        
        
        
        if(hWnd==null) {
            System.out.println("create window failed!");
            System.out.println(""+ Kernel32.INSTANCE.GetLastError() + "\n"+Kernel32Util.getLastErrorMessage());
            return false;
        }
        
        System.out.println("the hwnd of this window is " + hWnd.getPointer().toString());
        User32.INSTANCE.ShowWindow(hWnd, WinUser.SW_SHOWNORMAL);
        User32.INSTANCE.UpdateWindow(hWnd);

        WinUser.MSG msg = new WinUser.MSG();
        while (User32.INSTANCE.GetMessage(msg, hWnd, 0, 0)!=0) {
            User32.INSTANCE.TranslateMessage(msg);
            User32.INSTANCE.DispatchMessage(msg);
        }

        return true;
    }
}

public class JnaDemo {
	public static void main(String[] args) {
		CWindow w = new CWindow();
		w.Run();
	}
}
