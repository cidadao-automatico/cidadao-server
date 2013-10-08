package models;

import play.api.{Logger, Application}
import securesocial.core._
import securesocial.core.providers.Token
import securesocial.core.IdentityId

class UserService(application: Application) extends UserServicePlugin(application) {
  /**
   * Finds a user that maches the specified id
   *
   * @param id the user id
   * @return an optional user
   */
  def find(id: IdentityId):Option[Identity] = {
    User.findByOAuthId(id.userId) match{
      case Some(dbUser) =>
        Option.apply(new SocialUser(new IdentityId(dbUser.oauthId, dbUser.oauthProvider),
          dbUser.firstName, dbUser.lastName, String.format("%s %s", dbUser.firstName, dbUser.lastName),
          dbUser.email, None, new AuthenticationMethod("oauth1"), None,None,None))
      case _ => None
    }
    
    
    // implement me
  }

  /**
   * Finds a user by email and provider id.
   *
   * Note: If you do not plan to use the UsernamePassword provider just provide en empty
   * implementation.
   *
   * @param email - the user email
   * @param providerId - the provider id
   * @return
   */
  def findByEmailAndProvider(email: String, providerId: String):Option[Identity] = None

  /**
   * Saves the user.  This method gets called when a user logs in.
   * This is your chance to save the user information in your backing store.
   * @param user
   */
  def save(user: Identity):Identity = {
    User.save(user.firstName, user.lastName, user.email, user.identityId.userId, user.identityId.providerId, null, null
      ,null,null,User.NORMAL_TYPE)

    var dbUser=User.findByOAuthId(user.identityId.userId).get
    new SocialUser(new IdentityId(dbUser.oauthId, dbUser.oauthProvider),
          dbUser.firstName, dbUser.lastName, String.format("%s %s", dbUser.firstName, dbUser.lastName),
          dbUser.email, None, new AuthenticationMethod("oauth1"), None,None,None)
  }

  /**
   * Saves a token.  This is needed for users that
   * are creating an account in the system instead of using one in a 3rd party system.
   *
   * Note: If you do not plan to use the UsernamePassword provider just provide en empty
   * implementation
   *
   * @param token The token to save
   * @return A string with a uuid that will be embedded in the welcome email.
   */
  def save(token: Token) = {
    // implement me
  }


  /**
   * Finds a token
   *
   * Note: If you do not plan to use the UsernamePassword provider just provide en empty
   * implementation
   *
   * @param token the token id
   * @return
   */
  def findToken(token: String): Option[Token] = None

  /**
   * Deletes a token
   *
   * Note: If you do not plan to use the UsernamePassword provider just provide en empty
   * implementation
   *
   * @param uuid the token id
   */
  def deleteToken(uuid: String) {
    // implement me
  }

  /**
   * Deletes all expired tokens
   *
   * Note: If you do not plan to use the UsernamePassword provider just provide en empty
   * implementation
   *
   */
  def deleteExpiredTokens() {
    // implement me
  }
}