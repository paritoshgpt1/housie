$(function() {
  const KEY = 'admin_sidebar_collapsed';
  const $body = $('body');

  function setCollapsed(collapsed) {
    if (collapsed) $body.addClass('sidebar-collapsed'); else $body.removeClass('sidebar-collapsed');
  }

  // Initialize from saved state
  try {
    const saved = localStorage.getItem(KEY);
    setCollapsed(saved === '1');
  } catch (e) {}

  // Toggle button in sidebar
  $(document).on('click', '#sidebarToggle', function() {
    const collapsed = !$body.hasClass('sidebar-collapsed');
    setCollapsed(collapsed);
    try { localStorage.setItem(KEY, collapsed ? '1' : '0'); } catch (e) {}
  });
});
