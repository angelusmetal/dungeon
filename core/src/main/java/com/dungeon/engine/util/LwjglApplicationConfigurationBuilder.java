package com.dungeon.engine.util;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglGraphics;
import com.badlogic.gdx.graphics.Color;
import com.typesafe.config.Optional;

public class LwjglApplicationConfigurationBuilder {
	@Optional private Boolean disableAudio;
	@Optional private Boolean useGL30;
	@Optional private Integer gles30ContextMajorVersion;
	@Optional private Integer gles30ContextMinorVersion;
	@Optional private Integer r, g, b, a;
	@Optional private Integer depth, stencil;
	@Optional private Integer samples;
	@Optional private Integer width, height;
	@Optional private Integer x, y;
	@Optional private Boolean fullscreen;
	@Optional private Integer overrideDensity;
	@Optional private Boolean vSyncEnabled;
	@Optional private String title;
	@Optional private Boolean forceExit;
	@Optional private Boolean resizable;
	@Optional private Integer audioDeviceSimultaneousSources;
	@Optional private Integer audioDeviceBufferSize;
	@Optional private Integer audioDeviceBufferCount;
	@Optional private Color initialBackgroundColor;
	@Optional private Integer foregroundFPS;
	@Optional private Integer backgroundFPS;
	@Optional private Boolean allowSoftwareMode;
	@Optional private String preferencesDirectory;
	@Optional private Files.FileType preferencesFileType;
	@Optional private LwjglGraphics.SetDisplayModeCallback setDisplayModeCallback;
	@Optional private Boolean useHDPI;

	public Boolean getDisableAudio() {
		return disableAudio;
	}

	public void setDisableAudio(Boolean disableAudio) {
		this.disableAudio = disableAudio;
	}

	public Boolean getUseGL30() {
		return useGL30;
	}

	public void setUseGL30(Boolean useGL30) {
		this.useGL30 = useGL30;
	}

	public Integer getGles30ContextMajorVersion() {
		return gles30ContextMajorVersion;
	}

	public void setGles30ContextMajorVersion(Integer gles30ContextMajorVersion) {
		this.gles30ContextMajorVersion = gles30ContextMajorVersion;
	}

	public Integer getGles30ContextMinorVersion() {
		return gles30ContextMinorVersion;
	}

	public void setGles30ContextMinorVersion(Integer gles30ContextMinorVersion) {
		this.gles30ContextMinorVersion = gles30ContextMinorVersion;
	}

	public Integer getR() {
		return r;
	}

	public void setR(Integer r) {
		this.r = r;
	}

	public Integer getG() {
		return g;
	}

	public void setG(Integer g) {
		this.g = g;
	}

	public Integer getB() {
		return b;
	}

	public void setB(Integer b) {
		this.b = b;
	}

	public Integer getA() {
		return a;
	}

	public void setA(Integer a) {
		this.a = a;
	}

	public Integer getDepth() {
		return depth;
	}

	public void setDepth(Integer depth) {
		this.depth = depth;
	}

	public Integer getStencil() {
		return stencil;
	}

	public void setStencil(Integer stencil) {
		this.stencil = stencil;
	}

	public Integer getSamples() {
		return samples;
	}

	public void setSamples(Integer samples) {
		this.samples = samples;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public Boolean getFullscreen() {
		return fullscreen;
	}

	public void setFullscreen(Boolean fullscreen) {
		this.fullscreen = fullscreen;
	}

	public Integer getOverrideDensity() {
		return overrideDensity;
	}

	public void setOverrideDensity(Integer overrideDensity) {
		this.overrideDensity = overrideDensity;
	}

	public Boolean getvSyncEnabled() {
		return vSyncEnabled;
	}

	public void setvSyncEnabled(Boolean vSyncEnabled) {
		this.vSyncEnabled = vSyncEnabled;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getForceExit() {
		return forceExit;
	}

	public void setForceExit(Boolean forceExit) {
		this.forceExit = forceExit;
	}

	public Boolean getResizable() {
		return resizable;
	}

	public void setResizable(Boolean resizable) {
		this.resizable = resizable;
	}

	public Integer getAudioDeviceSimultaneousSources() {
		return audioDeviceSimultaneousSources;
	}

	public void setAudioDeviceSimultaneousSources(Integer audioDeviceSimultaneousSources) {
		this.audioDeviceSimultaneousSources = audioDeviceSimultaneousSources;
	}

	public Integer getAudioDeviceBufferSize() {
		return audioDeviceBufferSize;
	}

	public void setAudioDeviceBufferSize(Integer audioDeviceBufferSize) {
		this.audioDeviceBufferSize = audioDeviceBufferSize;
	}

	public Integer getAudioDeviceBufferCount() {
		return audioDeviceBufferCount;
	}

	public void setAudioDeviceBufferCount(Integer audioDeviceBufferCount) {
		this.audioDeviceBufferCount = audioDeviceBufferCount;
	}

	public Color getInitialBackgroundColor() {
		return initialBackgroundColor;
	}

	public void setInitialBackgroundColor(Color initialBackgroundColor) {
		this.initialBackgroundColor = initialBackgroundColor;
	}

	public Integer getForegroundFPS() {
		return foregroundFPS;
	}

	public void setForegroundFPS(Integer foregroundFPS) {
		this.foregroundFPS = foregroundFPS;
	}

	public Integer getBackgroundFPS() {
		return backgroundFPS;
	}

	public void setBackgroundFPS(Integer backgroundFPS) {
		this.backgroundFPS = backgroundFPS;
	}

	public Boolean getAllowSoftwareMode() {
		return allowSoftwareMode;
	}

	public void setAllowSoftwareMode(Boolean allowSoftwareMode) {
		this.allowSoftwareMode = allowSoftwareMode;
	}

	public String getPreferencesDirectory() {
		return preferencesDirectory;
	}

	public void setPreferencesDirectory(String preferencesDirectory) {
		this.preferencesDirectory = preferencesDirectory;
	}

	public Files.FileType getPreferencesFileType() {
		return preferencesFileType;
	}

	public void setPreferencesFileType(Files.FileType preferencesFileType) {
		this.preferencesFileType = preferencesFileType;
	}

	public LwjglGraphics.SetDisplayModeCallback getSetDisplayModeCallback() {
		return setDisplayModeCallback;
	}

	public void setSetDisplayModeCallback(LwjglGraphics.SetDisplayModeCallback setDisplayModeCallback) {
		this.setDisplayModeCallback = setDisplayModeCallback;
	}

	public Boolean getUseHDPI() {
		return useHDPI;
	}

	public void setUseHDPI(Boolean useHDPI) {
		this.useHDPI = useHDPI;
	}

	public LwjglApplicationConfiguration build() {
		LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
		if (disableAudio != null) {
			LwjglApplicationConfiguration.disableAudio = disableAudio;
		}
		if (useGL30 != null) {
			configuration.useGL30 = useGL30;
		}
		if (gles30ContextMajorVersion != null) {
			configuration.gles30ContextMajorVersion = gles30ContextMajorVersion;
		}
		if (gles30ContextMinorVersion != null) {
			configuration.gles30ContextMinorVersion = gles30ContextMinorVersion;
		}
		if (r != null) {
			configuration.r = r;
		}
		if (g != null) {
			configuration.g = g;
		}
		if (b != null) {
			configuration.b = b;
		}
		if (a != null) {
			configuration.a = a;
		}
		if (depth != null) {
			configuration.depth = depth;
		}
		if (stencil != null) {
			configuration.stencil = stencil;
		}
		if (samples != null) {
			configuration.samples = samples;
		}
		if (width != null) {
			configuration.width = width;
		}
		if (height != null) {
			configuration.height = height;
		}
		if (x != null) {
			configuration.x = x;
		}
		if (y != null) {
			configuration.y = y;
		}
		if (fullscreen != null) {
			configuration.fullscreen = fullscreen;
		}
		if (overrideDensity != null) {
			configuration.overrideDensity = overrideDensity;
		}
		if (vSyncEnabled != null) {
			configuration.vSyncEnabled = vSyncEnabled;
		}
		if (title != null) {
			configuration.title = title;
		}
		if (forceExit != null) {
			configuration.forceExit = forceExit;
		}
		if (resizable != null) {
			configuration.resizable = resizable;
		}
		if (audioDeviceSimultaneousSources != null) {
			configuration.audioDeviceSimultaneousSources = audioDeviceSimultaneousSources;
		}
		if (audioDeviceBufferSize != null) {
			configuration.audioDeviceBufferSize = audioDeviceBufferSize;
		}
		if (audioDeviceBufferCount != null) {
			configuration.audioDeviceBufferCount = audioDeviceBufferCount;
		}
		if (initialBackgroundColor != null) {
			configuration.initialBackgroundColor = initialBackgroundColor;
		}
		if (foregroundFPS != null) {
			configuration.foregroundFPS = foregroundFPS;
		}
		if (backgroundFPS != null) {
			configuration.backgroundFPS = backgroundFPS;
		}
		if (allowSoftwareMode != null) {
			configuration.allowSoftwareMode = allowSoftwareMode;
		}
		if (preferencesDirectory != null) {
			configuration.preferencesDirectory = preferencesDirectory;
		}
		if (preferencesFileType != null) {
			configuration.preferencesFileType = preferencesFileType;
		}
		if (setDisplayModeCallback != null) {
			configuration.setDisplayModeCallback = setDisplayModeCallback;
		}
		if (useHDPI != null) {
			configuration.useHDPI = useHDPI;
		}
		return configuration;
	}
}
