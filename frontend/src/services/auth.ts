import Login from '../interfaces/login'
import Register from '../interfaces/register'
import { axiosPrivate } from './axiosPrivate'


export const postLogin = (data:Login) => {
  return axiosPrivate.post('/auth/login', data, {
  })

}


export const postRegister = (data:Register) => {
  return axiosPrivate.post('/auth/register', data, {
  })
}