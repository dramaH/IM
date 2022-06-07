<script>
export default {
  name: 'DramaTabs',
  props: {
    activeIndex: String
  },
  data() {
    return {
      active: this.activeIndex
    }
  },
  mounted() {
    if (!this.active) {
      this.active = this.$slots['tab-pane'][0].data.attrs.index
    }
  },
  methods: {
    _handleNavClick(index) {
      this.active = index
    }
  },
  render() {
    const pane = []
    const nav = []
    this.$slots['tab-pane'].map(vnode => {
      const { tab, index } = vnode.data.attrs
      pane.push(
        <div class='Drama-tabs-content__pane' v-show={this.active === index}>
          {vnode}
        </div>
      )
      nav.push(
        <div
          class={['Drama-tabs-nav__item', this.active === index && 'Drama-tabs-nav__item--active']}
          on-click={() => this._handleNavClick(index)}
        >
          {tab}
        </div>
      )
    })
    return (
      <div class='Drama-tabs'>
        <div class='Drama-tabs-content'>{pane}</div>
        <div class='Drama-tabs-nav'>{nav}</div>
      </div>
    )
  }
}
</script>
<style lang="stylus">
@import './../styles/utils/index';

pane-color = #f6f6f6;

+b(Drama-tabs) {
  background: pane-color;
}

+b(Drama-tabs-content) {
  width: 100%;
  height: 100%;
  padding: 15px;

  +e(pane) {
    // scrollbar-light()
    // overflow-y auto
    height: 100%;
    width: 100%;
  }
}

+b(Drama-tabs-nav) {
  display: flex;
  background: #eee;

  +e(item) {
    line-height: 38px;
    padding: 0 15px;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);

    +m(active) {
      background: pane-color;
    }
  }
}
</style>
