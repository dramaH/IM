/*
 * @Author: drama hly124971908@qq.com
 * @Date: 2022-05-28 15:35:25
 * @LastEditors: drama hly124971908@qq.com
 * @LastEditTime: 2022-06-07 14:42:03
 * @FilePath: /pc-internal/src/components/InnerChat/packages/index.js
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import Contextmenu from './directives/contextmenu'
import DramaTabs from './components/tabs'
import DramaPopover from './components/popover'
import DramaButton from './components/button'
import DramaBadge from './components/badge'
import DramaAvatar from './components/avatar'
import DramaContact from './components/contact'
import DramaEditor from './components/editor'
import DramaMessages from './components/messages'
import DramaMessageBasic from './components/message/basic'
import DramaMessageText from './components/message/text'
import DramaMessageImage from './components/message/image'
import DramaMessageFile from './components/message/file'
import DramaMessageEvent from './components/message/event'

import DramaIMUI from './components/index'
import './styles/common/index.styl'
const version = '1.4.2'
const components = [
  DramaIMUI,
  DramaContact,
  DramaMessages,
  DramaEditor,
  DramaAvatar,
  DramaBadge,
  DramaButton,
  DramaPopover,
  DramaTabs,
  DramaMessageBasic,
  DramaMessageText,
  DramaMessageImage,
  DramaMessageFile,
  DramaMessageEvent
]
const install = (Vue) => {
  Vue.directive('DramaContextmenu', Contextmenu)
  components.forEach((component) => {
    Vue.component(component.name, component)
  })
}

if (typeof window !== 'undefined' && window.Vue) {
  install(window.Vue)
}

export default {
  version,
  install
}
