package edu.miu.waa.onlineauctionapi.common;

public class Constants {
  public static final String API_URL_v1_PREFIX = "/api/v1";

  public static final String ENCODER_ID = "bcrypt";
  //    public static final String API_URL_PREFIX = "/api/v1/**";

  public static final String SIGNUP_URL = API_URL_v1_PREFIX + "/users";
  public static final String SIGNUP_ADMIN_URL = API_URL_v1_PREFIX + "/admins";
  public static final String TOKEN_URL = API_URL_v1_PREFIX + "/auth/token";

  public static final String PRODUCTS_URLs = API_URL_v1_PREFIX + "/products/**";
  public static final String ADMIN_URLs = API_URL_v1_PREFIX + "/admin/**";
  public static final String SELLER_PRODUCTS_URLs = API_URL_v1_PREFIX + "/seller/products/**";
  public static final String SELLER_PRODUCTS_STATICS_URLs =
      API_URL_v1_PREFIX + "/seller/products/statics/**";

  public static final String PRODUCTS_URL_PREFIX = API_URL_v1_PREFIX + "/products";
  public static final String SELLER_PRODUCTS_URL_PREFIX = API_URL_v1_PREFIX + "/seller/products";
  public static final String BIDS_URL_PREFIX = API_URL_v1_PREFIX + "/bids";
  public static final String ADMIN_URL_PREFIX = API_URL_v1_PREFIX + "/admin";

  public static final String AUTHORIZATION = "Authorization";
  public static final String TOKEN_PREFIX = "Bearer ";
  public static final String ROLE_CLAIM = "roles";
  public static final String NAME_CLAIM = "name";
  public static final String AUTHORITY_PREFIX = "ROLE_";
}
