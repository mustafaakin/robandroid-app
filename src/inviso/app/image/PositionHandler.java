package inviso.app.image;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import android.os.Environment;


public class PositionHandler {
	public ArrayList<Position> positions = new ArrayList<Position>();
	public int currentPosition = 0;
		
	public PositionHandler() {

	}
	
	public SceneDetectData findPlace(Mat frame, boolean blind, int position){
		SceneDetectData max = null;
		int maxValue = -1;
		Scene referenceScene = new Scene(frame); 
		for ( int i = 0; i < positions.size(); i++){
			Scene scene = positions.get(i).scene;
			SceneDetectData data = referenceScene.compare(scene, true);
			if ( data.homo_matches > maxValue){
				max = data;
				maxValue = data.homo_matches;
			}
		}
		return max;
	}
	
	public void readFiles(){
		File pathInfo = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		String filenameInfo = "infoFile.txt";
		File fileInfo = new File(pathInfo, filenameInfo);
		if (fileInfo.exists()) {
			try {
				Scanner scan = new Scanner(fileInfo);
				positions.clear();

				while (scan.hasNextLine()) {
					String[] txt = scan.nextLine().split("\t");
					String filename = txt[0];
					double angle = Double.parseDouble(txt[1]);
					boolean isChargingDock = Integer.parseInt(txt[2]) == 0 ? false : true;

					File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
					File file = new File(path, filename);

					Mat mat = Highgui.imread(file.getAbsolutePath());
					if (!mat.empty()) {
						Scene scene = new Scene(mat);						
						Position pos = new Position(scene, isChargingDock, angle);
						positions.add(pos);
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
