package pimpmygps.beans;

import java.lang.Runtime.Version;

import pimpmygps.utils.Fonctions;

public class SystemGps {
	String freeMemoryGb;
	String useMemoryGb;
	String totalMemoryGb;
	Version javaVersion;
	String version;
	String availableVersion;
	Integer processorAvailable;
	
	
	public SystemGps initialize()
	{
		freeMemoryGb=Fonctions.getFreeMemory();
		useMemoryGb=Fonctions.getUsedMemory();
		totalMemoryGb=Fonctions.getTotalMemory();
		javaVersion=Runtime.getRuntime().version();
		availableVersion="";
		processorAvailable=Runtime.getRuntime().availableProcessors();
		return this;
	}
	
	public String getFreeMemoryGb() {
		return freeMemoryGb;
	}
	public void setFreeMemoryGb(String freeMemoryGb) {
		this.freeMemoryGb = freeMemoryGb;
	}
	public String getUseMemoryGb() {
		return useMemoryGb;
	}
	public void setUseMemoryGb(String useMemoryGb) {
		this.useMemoryGb = useMemoryGb;
	}
	public String getTotalMemoryGb() {
		return totalMemoryGb;
	}
	public void setTotalMemoryGb(String totalMemoryGb) {
		this.totalMemoryGb = totalMemoryGb;
	}
	public Version getJavaVersion() {
		return javaVersion;
	}
	public void setJavaVersion(Version javaVersion) {
		this.javaVersion = javaVersion;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getAvailableVersion() {
		return availableVersion;
	}
	public void setAvailableVersion(String availableVersion) {
		this.availableVersion = availableVersion;
	}
	public Integer getProcessorAvailable() {
		return processorAvailable;
	}
	public void setProcessorAvailable(Integer processorAvailable) {
		this.processorAvailable = processorAvailable;
	}

}
