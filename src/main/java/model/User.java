package model;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * User is an abstraction, containing common state and behaviours shared by different kinds of users:
 * Consumers, EntertainmentProviders, and GovernmentRepresentatives.
 */
public abstract class User extends Object {

    private String email;
    private String password;
    private String paymentAccountEmail;

    /**
     * Create a new User and save the user email and payment account email, but do not save the password in plaintext!
     * This would be a security disaster, because a hacker could easily get it out of the application.
     * Instead, hash the user password using BCrypt - in other words, transform the password into another string,
     * using a library that always turns the same password into the same string (called a hash), but there is no way easy
     * way to reverse the hash back to the password. This allows to check whether someone entered the correct password
     * without ever storing the password itself, only a hash that cannot be easily reversed.
     * 
     * @param email new user email address (will be used to log in with)
     * @param password new user password (will be hashed and the hash will be used to verify future logins)
     * @param paymentAccountEmail email address that corresponds to an account in the PaymentSystem
     * @See Also: BCrypt library on GitHub that integrates with Gradle
     */


    protected User(String email, String password, String paymentAccountEmail) {
        this.email = email;
        this.paymentAccountEmail = paymentAccountEmail;
        // stores password as a hash using BCrypt
        this.password = BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }



    public String getEmail() {
        return this.email;
    }


    public void setEmail(String newEmail) {
        this.email = newEmail;
    }

    /**
     * Check whether the stored password hash matches the hash of the specified password.
     * You can use return BCrypt.verifyer().verify(password.toCharArray(), passwordHash).verified;
     * 
     * @param password password to hash and check
     * @return True if the passwords match and false if they do not
     */
    public boolean checkPasswordMatch(String password) {
        return BCrypt.verifyer().verify(password.toCharArray(), this.password).verified;
    }

    /**
     * Update the stored password hash to a new one, corresponding to the specified password.
     * You can use passwordHash = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray());
     * 
     * @param newPassword password to update the stored hash with
     */
    public void updatePassword(String newPassword) {
        this.password = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray());
    }

    protected String getPassword(){
        return this.password;
    }


    public String getPaymentAccountEmail() {
        return this.paymentAccountEmail;
    }


    public void setPaymentAccountEmail(String newPaymentAccountEmail) {
        this.paymentAccountEmail = newPaymentAccountEmail;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", paymentAccountEmail='" + paymentAccountEmail + '\'' +
                '}';
    }
}
