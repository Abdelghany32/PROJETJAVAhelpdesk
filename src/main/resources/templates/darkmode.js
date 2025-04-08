<html xmlns:th="http://www.thymeleaf.org"
      th:attr="data-server-theme='light'">
<head>
    <title>Connexion Étudiant</title>
    <div th:replace="~{base :: head}"></div>
    <style>
        .login-container {
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }
        
        .login-content {
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 2rem;
        }
        
        .login-card {
            width: 100%;
            max-width: 450px;
            background-color: var(--card-background);
            border-radius: 16px;
            box-shadow: var(--shadow-lg);
            overflow: hidden;
            animation: fadeIn 0.8s ease-out;
        }
        
        .login-header {
            background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
            color: white;
            padding: 2rem;
            text-align: center;
            position: relative;
        }
        
        .login-header::after {
            content: '';
            position: absolute;
            bottom: -10px;
            left: 50%;
            transform: translateX(-50%);
            width: 60%;
            height: 20px;
            background-color: var(--card-background);
            border-radius: 50% 50% 0 0;
        }
        
        .login-title {
            font-size: 1.8rem;
            font-weight: 700;
            margin-bottom: 0.5rem;
        }
        
        .login-subtitle {
            font-size: 1rem;
            opacity: 0.9;
        }
        
        .login-form {
            padding: 2rem;
            box-shadow: none;
            margin: 0;
            background: transparent;
        }
        
        .form-floating {
            margin-bottom: 1.5rem;
        }
        
        .form-control {
            border-radius: 8px;
            padding: 1rem 0.75rem;
            height: calc(3.5rem + 2px);
            font-size: 1rem;
            border: 2px solid var(--border-color);
            background-color: var(--background-color);
            transition: all 0.3s ease;
        }
        
        .form-control:focus {
            border-color: var(--primary-color);
            box-shadow: 0 0 0 0.25rem rgba(99, 102, 241, 0.25);
            transform: translateY(-2px);
        }
        
        .form-label {
            padding: 1rem 0.75rem;
        }
        
        .btn-login {
            width: 100%;
            padding: 1rem;
            font-size: 1.1rem;
            font-weight: 600;
            border-radius: 8px;
            background: linear-gradient(90deg, var(--primary-color), var(--secondary-color));
            border: none;
            color: white;
            margin-top: 1rem;
            transition: all 0.3s ease;
        }
        
        .btn-login:hover {
            transform: translateY(-3px);
            box-shadow: 0 4px 12px rgba(99, 102, 241, 0.4);
        }
        
        .login-footer {
            text-align: center;
            margin-top: 1.5rem;
            color: var(--gray-color);
        }
        
        .login-footer a {
            color: var(--primary-color);
            text-decoration: none;
            font-weight: 600;
            transition: all 0.3s ease;
            position: relative;
        }
        
        .login-footer a::after {
            content: '';
            position: absolute;
            width: 100%;
            height: 2px;
            bottom: -2px;
            left: 0;
            background-color: var(--primary-color);
            transform: scaleX(0);
            transform-origin: bottom right;
            transition: transform 0.3s ease-out;
        }
        
        .login-footer a:hover::after {
            transform: scaleX(1);
            transform-origin: bottom left;
        }
        
        .input-icon {
            position: absolute;
            top: 50%;
            right: 1rem;
            transform: translateY(-50%);
            color: var(--gray-color);
            transition: all 0.3s ease;
        }
        
        .form-control:focus + .input-icon {
            color: v