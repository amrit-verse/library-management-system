
document.querySelectorAll('.btn').forEach(btn=>{
    btn.addEventListener('mouseenter',()=>{
        btn.style.transform='translateY(-3px)';
    });
});

function showToast(message){
    const toast=document.createElement('div');
    toast.innerText=message;
    toast.style.position='fixed';
    toast.style.bottom='20px';
    toast.style.right='20px';
    toast.style.padding='1rem 1.2rem';
    toast.style.background='#2563eb';
    toast.style.color='white';
    toast.style.borderRadius='12px';
    toast.style.zIndex='999';
    document.body.appendChild(toast);

    setTimeout(()=>toast.remove(),3000);
}
