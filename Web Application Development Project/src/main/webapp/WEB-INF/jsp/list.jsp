<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Comments and Files</title>
    <style>
        .container {
            display: flex;
            flex-direction: column;
            gap: 20px;
            padding: 20px;
        }
        .section {
            padding: 20px;
            border: 1px solid #ccc;
            border-radius: 5px;
        }
        .comment-item, .file-item {
            margin-bottom: 15px;
            padding: 10px;
            border: 1px solid #eee;
        }
        .admin-section {
            background-color: #f9f9f9;
            padding: 15px;
            border-radius: 5px;
            margin-top: 20px;
        }
        .file-actions {
            margin-top: 10px;
        }
        .file-actions a {
            margin-right: 10px;
            text-decoration: none;
            color: #0066cc;
        }
        .file-actions a:hover {
            text-decoration: underline;
        }
        .form-group {
            margin-bottom: 15px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
        }
        .form-group input[type="text"],
        .form-group input[type="file"] {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        button {
            padding: 8px 15px;
            background-color: #0066cc;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        button:hover {
            background-color: #0052a3;
        }
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        .logout-button {
            background-color: #dc3545;
        }
        .logout-button:hover {
            background-color: #c82333;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Comments and Files</h1>
            <form action="/CSApp/logout" method="post">
                <button type="submit" class="logout-button">Logout</button>
            </form>
        </div>

        <!-- Comments Section -->
        <div class="section">
            <h2>Comments</h2>
            <div class="comment-section">
                <c:forEach items="${commentDatabase}" var="comment">
                    <div class="comment-item">
                        <h3>${comment.subject}</h3>
                        <p>${comment.body}</p>
                        <p><small>By: ${comment.customerName}</small></p>
                        <c:if test="${pageContext.request.isUserInRole('ROLE_ADMIN')}">
                            <div class="file-actions">
                                <a href="delete/${comment.id}" onclick="return confirm('Are you sure?')">Delete</a>
                            </div>
                        </c:if>
                    </div>
                </c:forEach>
            </div>
        </div>

        <!-- Admin Files Section -->
        <div class="section">
            <h2>Admin Files</h2>
            <c:if test="${pageContext.request.isUserInRole('ROLE_ADMIN')}">
                <div class="admin-section">
                    <h3>Upload New File</h3>
                    <form action="/CSApp/admin/upload" method="post" enctype="multipart/form-data">
                        <div class="form-group">
                            <label for="description">Description:</label>
                            <input type="text" id="description" name="description" required>
                        </div>
                        <div class="form-group">
                            <label for="file">File:</label>
                            <input type="file" id="file" name="file" required>
                        </div>
                        <button type="submit">Upload</button>
                    </form>
                </div>
            </c:if>
            
            <div class="file-section">
                <c:forEach items="${adminFiles}" var="file">
                    <div class="file-item">
                        <h3>${file.fileName}</h3>
                        <p>${file.description}</p>
                        <div class="file-actions">
                            <a href="/CSApp/admin/${file.id}/download">Download</a>
                            <c:if test="${pageContext.request.isUserInRole('ROLE_ADMIN')}">
                                <a href="/CSApp/admin/${file.id}/delete" onclick="return confirm('Are you sure?')">Delete</a>
                            </c:if>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>

        <!-- Add Comment Section -->
        <div class="section">
            <h2>Add Comment</h2>
            <form action="/CSApp/comment/add" method="post">
                <div class="form-group">
                    <label for="subject">Subject:</label>
                    <input type="text" id="subject" name="subject" required>
                </div>
                <div class="form-group">
                    <label for="body">Content:</label>
                    <input type="text" id="body" name="body" required>
                </div>
                <button type="submit">Add Comment</button>
            </form>
        </div>
    </div>
</body>
</html>