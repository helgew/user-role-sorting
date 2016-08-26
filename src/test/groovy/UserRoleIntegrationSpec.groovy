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
    UserRole adminAdminRole
    UserRole staffStaffRole

    def setup() {
        User admin = new User(username: 'admin', password: 'password').save()
        User staff = new User(username: 'staff', password: 'password').save()

        Role adminRole = new Role(authority: 'ROLE_ADMIN').save()
        Role staffRole = new Role(authority: 'ROLE_STAFF').save()

        adminAdminRole = UserRole.create admin, adminRole
        staffStaffRole = UserRole.create staff, staffRole
    }

    void "Test sorting by role.authority"() {
        when: "we execute a query that orders by role.authority"
        List<UserRole> l = UserRole.where {
            role.authority in ['ROLE_ADMIN', 'ROLE_STAFF']
            order('role.authority', 'desc')
        }.list()

        then: "we have a sorted list"
        l.size() == 2
        l[0].id == staffStaffRole.id
    }

    void "Test sorting by user.username"() {
        when: "we execute a query that orders by user.username"
        List<UserRole> l = UserRole.where {
            role.authority in ['ROLE_ADMIN', 'ROLE_STAFF']
            order('user.username', 'desc')
        }.list()

        then: "we have a sorted list"
        l.size() == 2
        l[0].id == staffStaffRole.id
    }

    void "Test querying by username first"() {
        when: "we execute a query by username and sort by role"
        List<UserRole> l = UserRole.where {
            user.username in ['admin', 'staff']
            order('role.authority', 'desc')
        }.list()

        then: "we have a sorted list"
        l.size() == 2
        l[0].id == staffStaffRole.id
    }

    void "Test querying and sorting by username"() {
        when: "we execute a query by username and sort by role"
        List<UserRole> l = UserRole.where {
            user.username in ['admin', 'staff']
            order('user.username', 'desc')
        }.list()

        then: "we have a sorted list"
        l.size() == 2
        l[0].id == staffStaffRole.id
    }

    void "Test using aliases"() {
        when: "we include an alias for each table"
        List<UserRole> l = UserRole.where {
            createAlias('role', 'role')
            createAlias('user', 'user')
            role.authority in ['ROLE_ADMIN', 'ROLE_STAFF']
            order('role.authority', 'desc')
        }.list()

        then: "we have a sorted list"
        l.size() == 2
        l[0].id == staffStaffRole.id
    }

    void "Test using aliases with inList"() {
        when: "we include an alias for each table and use inList"
        List<UserRole> l = UserRole.where {
            createAlias('role', 'role')
            createAlias('user', 'user')
            inList('role.authority', ['ROLE_ADMIN', 'ROLE_STAFF'])
            order('role.authority', 'desc')
        }.list()

        then: "we have a sorted list"
        l.size() == 2
        l[0].id == staffStaffRole.id
    }

    // This test illustrates another possible work-around but the approach
    // requires additional logic when the sort column is passed in dynamically
    void "Test querying and sorting by username with closure"() {
        when: "we execute a query by username and sort by role"
        List<UserRole> l = UserRole.where {
            user {
                username in ['admin', 'staff']
                order('username', 'desc')
            }
        }.list()

        then: "we have a sorted list"
        l.size() == 2
        l[0].id == staffStaffRole.id
    }
}
