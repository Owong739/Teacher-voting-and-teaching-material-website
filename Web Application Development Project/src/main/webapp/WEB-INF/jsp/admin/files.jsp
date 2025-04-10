<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Files</title>
    <style>
        .file-section {
            margin: 20px;
            padding: 20px;
            border: 1px solid #ccc;
            border-radius: 5px;
        }
        .file-list {
            margin-top: 20px;
        }
        .file-item {
            margin-bottom: 15px;
            padding: 10px;
            border: 1px solid #eee;
        }
        .comment-section {
            margin-left: 20px;
            padding: 10px;
            background-color: #f9f9f9;
        }
        .file-actions {
            margin-top: 10px;
        }
        .file-actions a {
            margin-right: 10px;
        }
    </style>
</head>
<body>
    <a href="/CSApp/comment/list">Back to List</a>

    <div class="file-section">
        <h2>Admin File Upload</h2>
        <form action="upload" method="post" enctype="multipart/form-data">
            <div>
                <label for="description">Description:</label>
                <input type="text" id="description" name="description" required>
            </div>
            <div>
                <label for="file">File:</label>
                <input type="file" id="file" name="file" required>
            </div>
            <button type="submit">Upload</button>
        </form>
    </div>

    <div class="file-section">
        <h2>Uploaded Files</h2>
        <div class="file-list">
            <c:forEach items="${files}" var="file">
                <div class="file-item">
                    <h3>${file.fileName}</h3>
                    <p>${file.description}</p>
                    <div class="file-actions">
                        <a href="${file.id}/download">Download</a>
                        <c:if test="${pageContext.request.isUserInRole('ROLE_ADMIN')}">
                            <a href="${file.id}/delete" onclick="return confirm('Are you sure?')">Delete</a>
                        </c:if>
                    </div>
                    
                    <div class="comment-section">
                        <h4>Comments</h4>
                        <c:if test="${pageContext.request.isUserInRole('ROLE_ADMIN')}">
                            <form action="${file.id}/comment" method="post">
                                <input type="text" name="content" placeholder="Add a comment" required>
                                <button type="submit">Add Comment</button>
                            </form>
                        </c:if>
                        <c:forEach items="${file.comments}" var="comment">
                            <div>
                                <strong>${comment.adminName}</strong>: ${comment.content}
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</body>
</html> 