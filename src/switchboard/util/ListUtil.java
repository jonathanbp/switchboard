package switchboard.util;

import java.util.List;

public class ListUtil {

	public static <T> T single(List<T> l) {
		synchronized (l) {
			if(l.size()>0) return l.get(0);
		} 
		return null;
	}

}
