package inviso.app.image;

import android.graphics.Bitmap;

public class SceneDetectData {
	public int original_key1;
	public int original_key2;	
	public int original_matches;
	public int dist_matches;
	public int homo_matches;
	public long elapsed;
	public int idx;
	
	Bitmap bmp;
	
	@Override
	public String toString() {
		String result = "";
		result +=   "Matched Image Index: " + idx;
		result += "\nTotal Matches: " + original_matches;
		result += "\nDistance Filtered Matches: " + dist_matches;
		result += "\nHomography Filtered Matches: " + homo_matches;
		result += "\nElapsed: (" + elapsed + " ms.)";
		return result;
	}
}
