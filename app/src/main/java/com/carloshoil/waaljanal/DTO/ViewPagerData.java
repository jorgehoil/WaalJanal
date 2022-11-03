package com.carloshoil.waaljanal.DTO;

public class ViewPagerData {
    public boolean lUrl;
    public String cUrl;
    public String cKey;

    public ViewPagerData()
    {

    }
    public ViewPagerData(boolean lUrl, String cUrl,String cKey)
    {
        this.lUrl=lUrl;
        this.cUrl=cUrl;
        this.cKey=cKey;
    }
}
