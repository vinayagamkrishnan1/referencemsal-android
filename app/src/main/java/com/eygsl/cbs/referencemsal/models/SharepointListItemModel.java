package com.eygsl.cbs.referencemsal.models;

public class SharepointListItemModel {

  private String name, country, city, imgURL, mobilePlatforms, serviceLine, homePageURL, environments, AppDescription;

  public String getImgURL(){
      return imgURL;
  }

  public void setImgURL(String imgURL){
      this.imgURL = imgURL;
  }

  public String getName() {
      return name;
  }

  public void setName(String Title) {
      this.name = Title;
  }

  public String getCity() {
      return city;
  }

  public void setCity(String AppType) {
      this.city = AppType;
  }


  public String getMobilePlatforms() { return mobilePlatforms; }

  public void setMobilePlatfroms(String MobilePlatforms) { this.mobilePlatforms = MobilePlatforms; }


  public String getserviceLine() { return serviceLine; }

  public void setserviceLine(String serviceLine) { this.serviceLine = serviceLine; }

  public String gethomePageURL() { return homePageURL; }

  public void sethomePageURL(String homePageURL) { this.homePageURL = homePageURL; }

  public String getenvironments() { return environments; }

  public void setenvironments(String environments) { this.environments = environments; }



  public String getAppDescription() { return AppDescription; }

  public void setAppDescription(String AppDescription) { this.AppDescription = AppDescription; }

}
