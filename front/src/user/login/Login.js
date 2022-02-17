import React, { Component } from 'react';
import { Form,Button, Input, notification, Icon } from 'antd';
import { ACCESS_TOKEN } from '../../constants';
import { Link } from 'react-router-dom';
import { login } from '../../util/ApiUtil';
import './Login.css';

const FormItem = Form.Item;
class Login extends Component {
    // constructor(props){
    //     super(props)
    //     console.log("login page",props);
    // }
    render() {
        const AntWrappedLoginForm=Form.create()(LoginForm)
        return (
            <div className="login-container">
            <h1 className="page-title">Login</h1>
            <div className="login-content">
                <AntWrappedLoginForm onLogin={this.props.onLogin} />
            </div>
        </div>
        );
    }
}
class LoginForm extends Component {
    constructor(props){

    super(props)
    console.log("login form with props",props)
    this.handleSubmit=this.handleSubmit.bind(this);
    }
    handleSubmit(event){
        event.preventDefault(event);
            this.props.form.validateFields((err,values)=>{
                 console.log("logging values",values)
                 if(!err){
                    const loginRequest=Object.assign({},values);
                    console.log("loginrequst",loginRequest);
                    // return;
                    
                    login(loginRequest)
                    .then((response)=>{
                        localStorage.setItem(ACCESS_TOKEN,response.accessToken)
                        this.props.onLogin();
                        console.log("logging the response after a successful login",response);
                    }).catch(error=>{
                        if(error.status===401){
                            notification.error({
                                message:'Polling App',
                                description: 'Your Username or Password is incorrect. Please try again!'
                            });
                        }else{
                            notification.error({
                                message:'Polling App',
                                description: error.message || 'Sorry! Something went wrong. Please try again!'

                            })
                        }
                    })
                }
            })
        
    }
    render(){
        const {getFieldDecorator}=this.props.form;
        return(
            <Form onSubmit={this.handleSubmit} className="login-form">
                <FormItem>
                    {getFieldDecorator('usernameOrEmail',{
                        rules:[{required:true,message:'Please input your username or email'}],
                    })(
                        <Input 
                        prefix={<Icon type="user" />}
                        size="large"
                        name="usernameOrEmail" 
                        placeholder="Username or Email" />
                    )}
                </FormItem>
                <FormItem>
                {getFieldDecorator('password', {
                    rules: [{ required: true, message: 'Please input your Password!' }],
                })(
                    <Input 
                        prefix={<Icon type="lock" />}
                        size="large"
                        name="password" 
                        type="password" 
                        placeholder="Password"  />                        
                )}
                </FormItem>
                <FormItem>
                    <Button type="primary" htmlType="submit" size="large" className="login-form-button">Login</Button>
                    Or <Link to="/signup">register now!</Link>
                </FormItem>
            </Form>
        )
    }
}

export default Login;