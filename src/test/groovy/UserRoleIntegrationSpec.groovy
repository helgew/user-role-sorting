import com.example.Role
import com.example.User
import com.example.UserRole
import grails.test.mixin.Mock
import grails.test.mixin.integration.Integration
import grails.transaction.Transactional
import org.hibernate.QueryException
import spock.lang.Specification

@Integration
@Transactional
class UserRoleIntegrationSpec extends Specification {
    def setup() {
        User admin = new User(username: 'admin', password: 'password').save()
        User staff = new User(username: 'staff', password: 'password').save()

        Role adminRole = new Role(authority: 'ROLE_ADMIN').save()
        Role staffRole = new Role(authority: 'ROLE_STAFF').save()

        UserRole.create admin, adminRole
        UserRole.create staff, staffRole
    }

    void "Test sorting by role.authority"() {
        when: "we execute a query that orders by role.authority"
        UserRole.where {
            role.authority in ['ROLE_ADMIN', 'ROLE_STAFF']
            order('role.authority', 'asc')
        }.list()

        then: "an exception will be thrown"
        def e = thrown(QueryException)
        e.message == 'could not resolve property: role.authority of: com.example.UserRole'
    }

    void "Test sorting by user.username"() {
        when: "we execute a query that orders by user.username"
        UserRole.where {
            role.authority in ['ROLE_ADMIN', 'ROLE_STAFF']
            order('user.username', 'asc')
        }.list()

        then: "all is well"
        noExceptionThrown()
    }

    void "Test querying by username first"() {
        when: "we execute a query by username and sort by role"
        UserRole.where {
            user.username in ['admin', 'staff']
            order('role.authority', 'asc')
        }.list()

        then: "all is well"
        noExceptionThrown()
    }

    void "Test querying and sorting by username"() {
        when: "we execute a query by username and sort by role"
        UserRole.where {
            user.username in ['admin', 'staff']
            order('user.username', 'asc')
        }.list()

        then: "an exception will be thrown"
        def e = thrown(QueryException)
        e.message == 'could not resolve property: user.username of: com.example.UserRole'
    }

    void "Test using aliases"() {
        when: "we include an alias for each table"
        UserRole.where {
            createAlias('role', 'role')
            createAlias('user', 'user')
            role.authority in ['ROLE_ADMIN', 'ROLE_STAFF']
            order('role.authority', 'asc')
        }.list()

        then: "an exception will be thrown"
        def e = thrown(NullPointerException)
    }

    void "Test using aliases with inList"() {
        when: "we include an alias for each table and use inList"
        UserRole.where {
            createAlias('role', 'role')
            createAlias('user', 'user')
            inList('role.authority', ['ROLE_ADMIN', 'ROLE_STAFF'])
            order('role.authority', 'asc')
        }.list()

        then: "all is well"
        noExceptionThrown()
    }
}
