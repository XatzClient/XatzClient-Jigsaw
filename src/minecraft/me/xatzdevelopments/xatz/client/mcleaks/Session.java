/*    */ package me.xatzdevelopments.xatz.client.mcleaks;
/*    */ 
/*    */ public class Session {
/*    */   private final String username;
/*    */   
/*    */   private final String token;
/*    */   
/*    */   public Session(String username, String token) {
/*  9 */     this.username = username;
/* 10 */     this.token = token;
/*    */   }
/*    */   
/*    */   public String getUsername() {
/* 14 */     return this.username;
/*    */   }
/*    */   
/*    */   public String getToken() {
/* 18 */     return this.token;
/*    */   }
/*    */ }


