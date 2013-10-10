/**
 * Copyright 2012 Jorge Aliss (jaliss at gmail dot com) - twitter: @jaliss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package service

import play.api.{Logger, Application}
import securesocial.core._
import securesocial.core.providers.Token
import securesocial.core.IdentityId
import models._

/**
 * A Sample In Memory user service in Scala
 *
 * IMPORTANT: This is just a sample and not suitable for a production environment since
 * it stores everything in memory.
 */
class VigiaUserService(application: Application) extends UserServicePlugin(application) {

 private var tokens = Map[String, Token]()
    
  def find(id: IdentityId):Option[Identity] = {
    User.findByOAuthId(id.userId) match{
      case Some(dbUser) =>
        Option.apply(new SocialUser(new IdentityId(dbUser.oauthId, dbUser.oauthProvider),
          dbUser.firstName, dbUser.lastName, String.format("%s %s", dbUser.firstName, dbUser.lastName),
          dbUser.email, None, new AuthenticationMethod("oauth2"), None,None,None))
      case _ => None
    }  
  }
  
  def findByEmailAndProvider(email: String, providerId: String):Option[Identity] = None
  
  def save(user: Identity):Identity = {

    //#TODO: Refactor here    
    user.email match {
      case Some(email) => 
        User.findByEmail(email) match {
          case Some(userObj) => None
          case _ =>  
              User.save(user.firstName, user.lastName, user.email, user.identityId.userId, user.identityId.providerId, None, None
              ,None,None,User.NORMAL_TYPE)            
        }
        
      case _ => 
        User.findByOAuthId(user.identityId.userId) match {
          case Some(userObj) => None
          case _ => None
              User.save(user.firstName, user.lastName, user.email, user.identityId.userId, user.identityId.providerId, None, None
              ,None,None,User.NORMAL_TYPE)            
        }        
    }

    user
  }

  def save(token: Token) = None

  def findToken(token: String): Option[Token] = None

  def deleteToken(uuid: String) = None

  def deleteTokens() = None

  def deleteExpiredTokens() = None

}
