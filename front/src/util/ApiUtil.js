import { T } from "antd/lib/upload/utils";
import { ACCESS_TOKEN } from "../constants";
import { API_BASE_URL, POLL_LIST_SIZE } from './../constants/index';

const request=(options)=>{

    const headers=new Headers({
        'Content-type':'application/json'
    });
    if(localStorage.getItem(ACCESS_TOKEN)){
        headers.append('Authorization','Bearer '+localStorage.getItem(ACCESS_TOKEN))
    }
    const defaults={headers:headers};
    options=Object.assign({},defaults,options);
    return fetch(options.url, options)
    .then(response => 
        response.json().then(json => {
            if(!response.ok) {
                return Promise.reject(json);
            }
            return json;
        })
    );
};


export function getAllPolls(page,size){
    page = page || 0;
    size = size || POLL_LIST_SIZE;
    return request({
        url: API_BASE_URL + "/polls/getall?page=" + page + "&size=" + size,
        method: 'GET'

    });
}
export function createPoll(pollData){
  return request({
    url: API_BASE_URL+`/polls/createpoll`,
    method:'POST',
    body: JSON.stringify(pollData)
  })
}
export function castVote(voteData){
    return request({
        url:API_BASE_URL+`/polls/`+voteData.pollId+`/votes`,
        method:'POST',
        body:JSON.stringify(voteData)
    });
}
export function login(loginrequest){
    return request({
        url: API_BASE_URL+`/auth/signin`,
        method: 'POST',
        body: JSON.stringify(loginrequest)

    })
}
export function signup(signuprequest){
    return request({
        url: API_BASE_URL+`/auth/signup`,
        method: 'POST',
        body:JSON.stringify(signuprequest)
    });
}
export function checkUsernameAvailability(username){
    return request({
        url:API_BASE_URL+`/users/checkUsernameAvailability/?username=`+username,
        method:'GET',
    });
}
export function checkEmailAvailability(email){
    return request({
        url: API_BASE_URL+`/users/checkUsernameAvailability/?username=`+email,
        method:'GET',
    });
}
export function getCurrentUser(){
    if(!localStorage.getItem(ACCESS_TOKEN)){
        return Promise.reject("No access token set");
    }
    return request({
        url:API_BASE_URL+`/users/currentuser`,
        method:'GET'

    });
}
export function getUserProfile(username){
    // return request({
    //     url:API_BASE_URL+`/users/profie/`+username,
    //     method:'GET'
    // });
    return request({
        url: API_BASE_URL + "/users/profie/" + username,
        method: 'GET'
    });
}
export function getUserCreatedPolls(username,page,size){
    page = page || 0;
    size = size || POLL_LIST_SIZE;
    return request({
        url:API_BASE_URL+`/users/${username}/polls?page=${page}&size=${size}`,
        method:'GET'
    })
}
export function getUserVotedPolls(username,page,size){
    page = page || 0;
    size = size || POLL_LIST_SIZE;
    return request({
        url:API_BASE_URL+`/users/${username}/votes?page=${page}&size=${size}`,
        method:'GET'
    });
}

