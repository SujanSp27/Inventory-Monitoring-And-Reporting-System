package com.testing.model;

import com.inventory.DataAccessObject.impl.UserDaoImpl;
import com.inventory.model.User;
import com.inventory.util.dbConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserDaoImplTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    private UserDaoImpl userDAO;
    private MockedStatic<dbConnection> mockedDBConnection;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // ✅ Mock static dbConnection.getConnection()
        mockedDBConnection = mockStatic(dbConnection.class);
        mockedDBConnection.when(dbConnection::getConnection).thenReturn(mockConnection);

        // ✅ Initialize DAO
        userDAO = new UserDaoImpl(mockConnection);
    }

    @AfterEach
    void tearDown() {
        if (mockedDBConnection != null) {
            mockedDBConnection.close();
        }
    }

    // ✅ Test: addUser() - success
    @Test
    void testAddUserSuccess() throws Exception {
        User user = new User(1, "ajit", "1234", "ajit@example.com", "ADMIN", false);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        userDAO.addUser(user);

        verify(mockPreparedStatement).setString(1, "ajit");
        verify(mockPreparedStatement).setString(2, "1234");
        verify(mockPreparedStatement).setString(3, "ajit@example.com");
        verify(mockPreparedStatement).setString(4, "ADMIN");
        verify(mockPreparedStatement).setBoolean(5, false);
        verify(mockPreparedStatement).executeUpdate();
    }

    // ✅ Test: addUser() - duplicate username/email
    @Test
    void testAddUserDuplicateUsername() throws Exception {
        User user = new User(2, "existingUser", "abcd", "exist@example.com", "USER", false);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        doThrow(new SQLIntegrityConstraintViolationException("Duplicate username"))
                .when(mockPreparedStatement).executeUpdate();

        assertDoesNotThrow(() -> userDAO.addUser(user));
        verify(mockPreparedStatement).executeUpdate();
    }

    // ✅ Test: getUserByUsername() - success
    @Test
    void testGetUserByUsernameSuccess() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("username")).thenReturn("ajit");
        when(mockResultSet.getString("password")).thenReturn("1234");
        when(mockResultSet.getString("email")).thenReturn("ajit@example.com");
        when(mockResultSet.getString("role")).thenReturn("ADMIN");
        when(mockResultSet.getBoolean("is_verified")).thenReturn(true);

        User result = userDAO.getUserByUsername("ajit");

        assertNotNull(result);
        assertEquals("ajit", result.getUsername());
        assertEquals("ADMIN", result.getRole());
        assertEquals("ajit@example.com", result.getEmail());
        assertTrue(result.isVerified());
    }

    // ✅ Test: getUserByUsername() - not found
    @Test
    void testGetUserByUsernameNotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        User result = userDAO.getUserByUsername("unknown");
        assertNull(result);
    }
}
