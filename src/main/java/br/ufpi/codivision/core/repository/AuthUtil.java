/**
 * 
 */
package br.ufpi.codivision.core.repository;

import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * @author Werney Ayala
 *
 */
public class AuthUtil {
	
	ISVNAuthenticationManager authManager;
	
	/**
	 * Default Constructor
	 */
	public AuthUtil(String userName, String password) {
		authManager = SVNWCUtil.createDefaultAuthenticationManager(userName, password.toCharArray());
	}
	
	public SVNClientManager getClientManager(){
		return SVNClientManager.newInstance(null, authManager);
	}

	/**
	 * @return the authManager
	 */
	public ISVNAuthenticationManager getAuthManager() {
		return authManager;
	}

	/**
	 * @param authManager the authManager to set
	 */
	public void setAuthManager(ISVNAuthenticationManager authManager) {
		this.authManager = authManager;
	}
	
}
