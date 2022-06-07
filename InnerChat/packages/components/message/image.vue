<script>
export default {
  name: 'DramaMessageImage',
  inheritAttrs: false,
  render() {
    return (
      <Drama-message-basic
        class='Drama-message-image'
        props={{ ...this.$attrs }}
        scopedSlots={{
          content: props => <img src={props.content} />
        }}
      />
    )
  }
}
</script>
<style lang="stylus">
@import './../../styles/utils/index';

+b(Drama-message-image) {
  +b(Drama-message) {
    +e(content) {
      padding: 0;
      cursor: pointer;
      overflow: hidden;

      img {
        max-width: 100%;
        min-width: 100px;
        display: block;
      }
    }
  }
}
</style>
