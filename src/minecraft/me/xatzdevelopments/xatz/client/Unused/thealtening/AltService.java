package me.xatzdevelopments.xatz.client.Unused.thealtening;

import java.util.*;

import me.xatzdevelopments.xatz.client.Unused.thealtening.utilities.*;

import java.net.*;

public class AltService
{
    private final ReflectionUtility userAuthentication;
    private final ReflectionUtility minecraftSession;
    private EnumAltService currentService;
    
    public AltService() {
        this.userAuthentication = new ReflectionUtility("com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication");
        this.minecraftSession = new ReflectionUtility("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService");
    }
    
    public void switchService(final EnumAltService enumAltService) throws NoSuchFieldException, IllegalAccessException {
        if (this.currentService == enumAltService) {
            return;
        }
        this.reflectionFields(enumAltService.hostname);
        this.currentService = enumAltService;
    }
    
    private void reflectionFields(final String authServer) throws NoSuchFieldException, IllegalAccessException {
        final HashMap<String, URL> userAuthenticationModifies = new HashMap<String, URL>();
        final String useSecureStart = authServer.contains("thealtening") ? "http" : "https";
        userAuthenticationModifies.put("ROUTE_AUTHENTICATE", this.constantURL(useSecureStart + "://authserver." + authServer + ".com/authenticate"));
        userAuthenticationModifies.put("ROUTE_INVALIDATE", this.constantURL(useSecureStart + "://authserver" + authServer + "com/invalidate"));
        userAuthenticationModifies.put("ROUTE_REFRESH", this.constantURL(useSecureStart + "://authserver." + authServer + ".com/refresh"));
        userAuthenticationModifies.put("ROUTE_VALIDATE", this.constantURL(useSecureStart + "://authserver." + authServer + ".com/validate"));
        userAuthenticationModifies.put("ROUTE_SIGNOUT", this.constantURL(useSecureStart + "://authserver." + authServer + ".com/signout"));
        userAuthenticationModifies.forEach((key, value) -> {
            try {
                this.userAuthentication.setStaticField(key, value);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return;
        });
        this.userAuthentication.setStaticField("BASE_URL", useSecureStart + "://authserver." + authServer + ".com/");
        this.minecraftSession.setStaticField("BASE_URL", useSecureStart + "://sessionserver." + authServer + ".com/session/minecraft/");
        this.minecraftSession.setStaticField("JOIN_URL", this.constantURL(useSecureStart + "://sessionserver." + authServer + ".com/session/minecraft/join"));
        this.minecraftSession.setStaticField("CHECK_URL", this.constantURL(useSecureStart + "://sessionserver." + authServer + ".com/session/minecraft/hasJoined"));
        this.minecraftSession.setStaticField("WHITELISTED_DOMAINS", new String[] { ".minecraft.net", ".mojang.com", ".thealtening.com" });
    }
    
    private URL constantURL(final String url) {
        try {
            return new URL(url);
        }
        catch (MalformedURLException ex) {
            throw new Error("Couldn't create constant for " + url, ex);
        }
    }
    
    public EnumAltService getCurrentService() {
        if (this.currentService == null) {
            this.currentService = EnumAltService.MOJANG;
        }
        return this.currentService;
    }
    
    public enum EnumAltService
    {
        MOJANG("mojang"), 
        THEALTENING("thealtening");
        
        String hostname;
        
        private EnumAltService(final String hostname) {
            this.hostname = hostname;
        }
    }
}
