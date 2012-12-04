package de.versus.efe.demo;


import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import de.efe.demo.R;
import de.versus.efe.EmbeddedFileExplorerConstants;
import de.versus.efe.FilePersistence;
import de.versus.efe.GenericFileExplorer;
import de.versus.efe.VersusPreferences;

public class EfeDemo extends Activity {
	
	private static final String FILE_BROWSING_ENABLED_CACHE_KEY = "fbe";
	private static final String FILE_BROWSING_DIRECTORY_CACHE_KEY = "fbd";
	
	private GenericFileExplorer fileExplorer = null;
	private int selectedFileIndex = EmbeddedFileExplorerConstants.INVALID_POSITION;
  	
	
	/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.main);
      
      // OPTIONAL: getting an existing selection index
      selectedFileIndex = (savedInstanceState != null 
      		&& savedInstanceState.containsKey("efeSelectedFileIndexCache")) 
				? savedInstanceState.getInt("efeSelectedFileIndexCache") 
				: EmbeddedFileExplorerConstants.INVALID_POSITION;
  }
    
  @Override
  protected void onResume() {
  	// RECOMMENDED: initialize the file explorer in onResume
  	if (fileExplorer == null) {
			initializeFileExplorer();
		}
  	setFileBrowserVisibilty(fileExplorer.isFileExplorerEnabled());
  	
  	// OPTIONAL: since the state has been cached, we can resume browsing
  	if (fileExplorer.isFileExplorerEnabled()) {
  		fileExplorer.initializeFileExplorer();
  	}
  	
  	super.onResume();
  }
  
  @Override
  protected void onPause() {
  	// OPTIONAL: caching the file explorers state
  	if (fileExplorer != null) {
  		saveFileBrowsingState(fileExplorer.isFileExplorerEnabled());
  	}
  	super.onPause();
  }  
  
  @Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// OPTIONAL: caching the selected file index
		outState.putInt("efeSelectedFileIndexCache", fileExplorer.getSelectedFileIndex());
	}
  
  @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// OPTIONAL: disable the file explorer before leaving the activity with the back button
			if (fileExplorer.isFileExplorerEnabled()) {
				fileExplorerCancelExploring(null);
				return true;
			} else {
				return super.onKeyDown(keyCode, event);
			}
		}
		return super.onKeyDown(keyCode, event);
	}
  
  
  public void toogleFileExplorer(View view) {
  	if (fileExplorer.isFileExplorerEnabled()) {
  		fileExplorerCancelExploring(null);
  	} else {
  		startBrowsing();
  	}
  	
  }
  
  
  
  private void initializeFileExplorer() {
		fileExplorer = new GenericFileExplorer(this, 
				findViewById(R.id.file_explorer_container), selectedFileIndex, "no access msg");
		VersusPreferences versusPreferences = new VersusPreferences();
		fileExplorer.setFileExplorerEnabled(versusPreferences.getPreferenceValue(this,
				FILE_BROWSING_ENABLED_CACHE_KEY, false));
		fileExplorer.setFileBrowsingDirectory(versusPreferences.getPreferenceValue(this,
				FILE_BROWSING_DIRECTORY_CACHE_KEY, null));
	}
  
  
    
	private void startBrowsing() {
		fileExplorer.setFileExplorerEnabled(true);

		FilePersistence filePersistence = new FilePersistence();
		if (filePersistence.isExternalStorageAvailable()) {
			fileExplorer.initializeFileExplorer();
		} else {
			Toast.makeText(this, "TODO: no access msg", Toast.LENGTH_SHORT).show();
			fileExplorer.setFileExplorerEnabled(false);
		}
		setFileBrowserVisibilty(fileExplorer.isFileExplorerEnabled());
	}

	public void fileExplorerCancelExploring(View view) {
		fileExplorer.setFileExplorerEnabled(false);
		saveFileBrowsingState(false);
		setFileBrowserVisibilty(false);
	}
	
	public void fileExplorerGoToParentDirectory(View view) {
		fileExplorer.goToParentDirectory();
	}
	
	public void fileExplorerUseSelectedFile(View view) {
		String selectedFilePath = fileExplorer.getSelectedFilePath();
		
		if (selectedFilePath != null) {
			
			// TODO: replace this toast with your action once a file has been selected
			Toast.makeText(this, "Selected file: " + selectedFilePath, Toast.LENGTH_SHORT).show();
			
		} else {
			Toast.makeText(this, "Selected file cannot be used.", Toast.LENGTH_SHORT).show();
		}
	}
		
	private void setFileBrowserVisibilty(boolean isFileBrowserEnabled) {
		findViewById(R.id.file_explorer_container).setVisibility(
				isFileBrowserEnabled ? View.VISIBLE : View.GONE);
	}
	
	private void saveFileBrowsingState(boolean isFileBrowserEnabled) {
		VersusPreferences versusPreferences = new VersusPreferences();
		versusPreferences.setPreferenceValue(this, isFileBrowserEnabled,
				FILE_BROWSING_ENABLED_CACHE_KEY);
		versusPreferences.setPreferenceValue(this, fileExplorer.getCurrentDirectoryPath(),
				FILE_BROWSING_DIRECTORY_CACHE_KEY);
	}
	
	private void resetFileBrowsingEnabledCache() {
		if (fileExplorer != null) {
			fileExplorer.setFileExplorerEnabled(false);
		}
		new VersusPreferences().removePreferenceValue(this, FILE_BROWSING_ENABLED_CACHE_KEY);
	}
}