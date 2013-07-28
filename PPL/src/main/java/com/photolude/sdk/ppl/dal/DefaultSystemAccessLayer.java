package com.photolude.sdk.ppl.dal;

import java.io.File;

public class DefaultSystemAccessLayer implements ISystemAccessLayer {

	public boolean FileExists(String path) {
		return new File(path).exists();
	}

}
